package dictionary.bot;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.json.packet.JsonPacketExtension;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.UUID;

/**
 * Created by harshit on 20/1/16.
 */
public class Bot {

    private static Bot bot;
    private XMPPTCPConnection xmppConnection;
    private Bot() throws XmppStringprepException {
        initializeConnection();
    }

    public static synchronized Bot getBot() throws XmppStringprepException {
        if (bot == null) {
            bot = new Bot();
        }
        return bot;
    }

    public void initializeConnection() throws XmppStringprepException {
        SmackConfiguration.setDefaultPacketReplyTimeout(30 * 1000);
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setUsernameAndPassword("fe9a971b-c130-44d4-84ab-090b90a56011", "qa");
        config.setResource("smack");
        config.setXmppDomain(JidCreate.domainBareFrom("ejabberd.sandwitch.in"));
        config.setDebuggerEnabled(true);
        config.setCompressionEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);
        config.setKeystorePath("src/main/java/res/ser.cert");
        try {
            TLSUtils.acceptAllCertificates(config);
            TLSUtils.disableHostnameVerificationForTlsCertificicates(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        xmppConnection = new XMPPTCPConnection(config.build());

        xmppConnection.setUseStreamManagement(true);
        xmppConnection.setUseStreamManagementResumption(true);
        xmppConnection.setPreferredResumptionTime(5 * 60);
        // Listen for messages
        StanzaFilter chatFilter = MessageTypeFilter.CHAT;
        // Listen for messages
        xmppConnection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
                Message message = (Message) packet;
                if (TriviaRetrofitAdapter.currentQuestionsMap.containsKey(message.getFrom().getLocalpartOrNull().toString())
                        && TriviaRetrofitAdapter.currentQuestionsMap.get(message.getFrom().getLocalpartOrNull().toString()).enqued) {
                    Observable.just(message)
                            .subscribeOn(Schedulers.computation())
                            .map(msg -> msg.getBody())
                            .filter(body -> body != null && body.length() > 0)
                            .map(filteredbody -> TriviaRetrofitAdapter.isValidOption(filteredbody))
                            .filter(ans -> ans > 0 && ans < 5)
                            .map(answer -> TriviaRetrofitAdapter.getTriviaRetrofitAdapter().validateAnswer(message.getFrom().getLocalpartOrNull().toString(), answer))
                            .subscribe(output -> {
                                        try {
                                            xmppConnection.sendStanza(generateMessage(message.getFrom(), Message.Type.chat, output));
                                        } catch (SmackException.NotConnectedException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> error.printStackTrace());
                } else {
                    Observable.just(message)
                            .subscribeOn(Schedulers.io())
                            .map(msg -> msg.getBody())
                            .filter(body -> body != null && body.length() > 0 && "Question".equalsIgnoreCase(body))
                            .map(filtered -> TriviaRetrofitAdapter.getTriviaRetrofitAdapter().getQuestion(message.getFrom().getLocalpartOrNull().toString()))
                            .limit(1)
                            .map(question -> TriviaRetrofitAdapter.generateString(question))
                            .filter(queslen -> queslen.length() > 0)
                            .subscribe(output -> {
                                        try {
                                            xmppConnection.sendStanza(generateMessage(message.getFrom(), Message.Type.chat, output));
                                        } catch (SmackException.NotConnectedException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> error.printStackTrace());
                }
            }
        }, chatFilter);
        try {
            xmppConnection.connect();
        } catch (InterruptedException | XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }
        /*
          Third parameter is resource. Used to support chat across different entities like Laptop/Mobile.
          Hard code resource to the client/libray - help us identify the client.
         */

        try {
            xmppConnection.login();
        } catch (InterruptedException | IOException | SmackException | XMPPException e) {
            e.printStackTrace();
        }
    }


    private Message generateMessage(Jid from, Message.Type chat, String s) {
        Message message = new Message();
        message.setStanzaId(UUID.randomUUID().toString());
        message.setType(chat);
        message.setTo(from);
        message.setBody(s);
        message.addExtension(generateJsonContainer(ChatType.TEXT));
        return message;
    }



    public JsonPacketExtension generateJsonContainer(ChatType chatType) {
        ChatMetaData  chatMetaData = new ChatMetaData(chatType.toString(), System.currentTimeMillis());
        JsonPacketExtension jsonPacketExtension = new JsonPacketExtension(chatMetaData.toJsonString());
        return jsonPacketExtension;
    }

}

package io.versehub.app.handler.user.authen.metamask;

import io.versehub.app.handler.user.authen.model.AddressWithNonce;
import io.vertx.core.json.JsonObject;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

public class SignatureHelper {
    public static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    private static AddressWithNonce parseMessage(String message) {
        return (AddressWithNonce)(new JsonObject(message)).mapTo(AddressWithNonce.class);
    }

    private static int getMessageLength(AddressWithNonce message) {
        String var10000 = message.getAddress();
        String signedMessage = var10000 + message.getNonce();
        return signedMessage.length();
    }

    public static AddressWithNonce recoveryAddress(String message, String signature) {
        AddressWithNonce result = parseMessage(message);
        String prefix = "\u0019Ethereum Signed Message:\n" + getMessageLength(result);
        byte[] msgHash = Hash.sha3((prefix + result.getAddress() + result.getNonce()).getBytes());
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v = (byte)(v + 27);
        }

        byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
        byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);
        Sign.SignatureData sd = new Sign.SignatureData(v, r, s);
        String addressRecovered = null;
        boolean match = false;

        for(int i = 0; i < 4; ++i) {
            ECDSASignature sig = new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS()));
            BigInteger publicKey = Sign.recoverFromSignature((byte)i, sig, msgHash);
            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                if (addressRecovered.equals(result.getAddress().toLowerCase())) {
                    match = true;
                    break;
                }
            }
        }

        if (!match) {
            throw new MetamaskSignatureException("Invalid signature");
        } else {
            return result;
        }
    }

    public SignatureHelper() {
    }
}

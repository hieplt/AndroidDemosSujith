package com.hydapps.androiddemossujith;

import android.content.Context;
import android.security.KeyChain;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyInfo;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

/**
 * Created by sujit on 23-12-2016.
 */

public class KeystoreHelper {

    private static final String LOG_TAG = "LOG_TAG";

    private static final String ALIAS = "alias_1";

    private Context mContext;
    private KeyStore mKeyStore;

    public KeystoreHelper(Context context) {
        mContext = context;
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createKey() {
        try {
            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();
            cal.add(Calendar.YEAR, 1);
            Date end = cal.getTime();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            generator.initialize(new KeyPairGeneratorSpec.Builder(mContext)
                    .setAlias(ALIAS).setSubject(new X500Principal("CN=sujith"))
                    .setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()))
                    .setStartDate(now)
                    .setEndDate(end).build());

            KeyPair pair = generator.generateKeyPair();

            KeyFactory factory = KeyFactory.getInstance("RSA", "AndroidKeyStore");
            X509EncodedKeySpec publicSpec = factory.getKeySpec(pair.getPublic(), X509EncodedKeySpec.class);
            String publicKeyStr = Base64.encodeToString(publicSpec.getEncoded(), Base64.NO_WRAP);

            Log.d(LOG_TAG, "public key: " + publicKeyStr);
            return  publicKeyStr;



        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isHardwareBacked() {
        KeyStore.PrivateKeyEntry entry = null;
        try {
            entry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(ALIAS, null);
            PublicKey key = entry.getCertificate().getPublicKey();

            KeyFactory factory = KeyFactory.getInstance("RSA", "AndroidKeyStore");
            KeyInfo info = factory.getKeySpec(key, KeyInfo.class);

            boolean hardwareBacked;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hardwareBacked = info.isInsideSecureHardware();
            } else {
                hardwareBacked = KeyChain.isBoundKeyAlgorithm("RSA");
            }
            return hardwareBacked;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getPublicKey() {
        try {
            KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(ALIAS, null);
            PublicKey key = entry.getCertificate().getPublicKey();

            KeyFactory factory = KeyFactory.getInstance("RSA", "AndroidKeyStore");

            X509EncodedKeySpec publicSpec = factory.getKeySpec(key, X509EncodedKeySpec.class);
            String publicKeyStr = Base64.encodeToString(publicSpec.getEncoded(), Base64.NO_WRAP);

            Log.d(LOG_TAG, "public key: " + publicKeyStr);
            return  publicKeyStr;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }


    // This WILL return NULL As private key can not be exported from "AndroidKeyStore"
    public String getPrivteKey() {
        try {
            KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(ALIAS, null);
            PrivateKey key = entry.getPrivateKey();
            if (key == null) {
                Log.e(LOG_TAG, "Private Key can't be extracted");
                return null;
            }

            KeyFactory factory = KeyFactory.getInstance("RSA", "AndroidKeyStore");
            PKCS8EncodedKeySpec spec = factory.getKeySpec(key, PKCS8EncodedKeySpec.class);
            String privateKeyStr = Base64.encodeToString(spec.getEncoded(), Base64.NO_WRAP);

            Log.d(LOG_TAG, "private key: " + privateKeyStr);
            return  privateKeyStr;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isKeyExist() {
        try {
            return mKeyStore.containsAlias(ALIAS);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }


    public String encrypt(String data) {

        try {
            KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(ALIAS, null);
            Cipher cip = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cip.init(Cipher.ENCRYPT_MODE, entry.getPrivateKey());
            byte[] encryptBytes = cip.doFinal(data.getBytes());
            String encryptedStr64 = Base64.encodeToString(encryptBytes, Base64.NO_WRAP);
            return encryptedStr64;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }


    // This decryption is done in the server
    public String decrypt(String publicKey, String encrypted64) {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(Base64.decode(publicKey, Base64.DEFAULT));
            PublicKey key = factory.generatePublic(publicSpec);
            Cipher cip = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cip.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cip.doFinal(Base64.decode(encrypted64, Base64.DEFAULT));
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }



}

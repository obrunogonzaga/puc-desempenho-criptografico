package org.pucpr.cripto.av;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
  
  
public class RSA512B {
  
  public static final String ALGORITHM = "RSA";
  
  /**
   * Local da chave privada no sistema de arquivos.
   */
  public static final String PATH_CHAVE_PRIVADA = "/Users/brunogsantos/keys/private-512.key";
  
  /**
   * Local da chave pública no sistema de arquivos.
   */
  public static final String PATH_CHAVE_PUBLICA = "/Users/brunogsantos/keys/public-512.key";
  
  /**
   * Gera a chave que contém um par de chave Privada e Pública usando 1025 bytes.
   * Armazena o conjunto de chaves nos arquivos private.key e public.key
   */
  public static void geraChave() {
    try {
    	
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
      keyGen.initialize(512);
      final KeyPair key = keyGen.generateKeyPair();
  
      File chavePrivadaFile = new File(PATH_CHAVE_PRIVADA);
      File chavePublicaFile = new File(PATH_CHAVE_PUBLICA);
  
      // Cria os arquivos para armazenar a chave Privada e a chave Publica
      if (chavePrivadaFile.getParentFile() != null) {
        chavePrivadaFile.getParentFile().mkdirs();
      }
       
      chavePrivadaFile.createNewFile();
  
      if (chavePublicaFile.getParentFile() != null) {
        chavePublicaFile.getParentFile().mkdirs();
      }
       
      chavePublicaFile.createNewFile();
  
      // Salva a Chave Pública no arquivo
      ObjectOutputStream chavePublicaOS = new ObjectOutputStream(
          new FileOutputStream(chavePublicaFile));
      chavePublicaOS.writeObject(key.getPublic());
      chavePublicaOS.close();
  
      // Salva a Chave Privada no arquivo
      ObjectOutputStream chavePrivadaOS = new ObjectOutputStream(
          new FileOutputStream(chavePrivadaFile));
      chavePrivadaOS.writeObject(key.getPrivate());
      chavePrivadaOS.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  
  }
  
  /**
   * Verifica se o par de chaves Pública e Privada já foram geradas.
   */
  public static boolean verificaSeExisteChavesNoSO() {
  
    File chavePrivada = new File(PATH_CHAVE_PRIVADA);
    File chavePublica = new File(PATH_CHAVE_PUBLICA);
  
    if (chavePrivada.exists() && chavePublica.exists()) {
      return true;
    }
     
    return false;
  }
  
  /**
   * Criptografa o texto puro usando chave pública.
   */
  public static byte[] criptografa(String texto, PrivateKey chave) {
    byte[] cipherText = null;
     
    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // Criptografa o texto puro usando a chave Púlica
      cipher.init(Cipher.ENCRYPT_MODE, chave);
      cipherText = cipher.doFinal(texto.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }
     
    return cipherText;
  }
  
  /**
   * Decriptografa o texto puro usando chave privada.
   */
  public static String decriptografa(byte[] texto, PublicKey chave) {
    byte[] dectyptedText = null;
     
    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // Decriptografa o texto puro usando a chave Privada
      cipher.init(Cipher.DECRYPT_MODE, chave);
      dectyptedText = cipher.doFinal(texto);
  
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  
    return new String(dectyptedText);
  }
  
  public static void main(String[] args) {
  
    try {
    	
      long tempoInicio = System.currentTimeMillis();
  
      if (!verificaSeExisteChavesNoSO()) {
        geraChave();
      }
  
      final String msgOriginal = "RSA é um algoritmo que deve o seu nome a três professores do MIT: Ronald Rivest, Adi Shamir e Leonard Adleman";
      ObjectInputStream inputStream = null;
  
      // Criptografa a Mensagem usando a Chave Privada
      inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
      final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();
      final byte[] textoCriptografado = criptografa(msgOriginal, chavePrivada);
      inputStream.close();
  
      // Decriptografa a Mensagem usando a Chave Pública
      inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
      final PublicKey chavePublica = (PublicKey) inputStream.readObject();
      final String textoPuro = decriptografa(textoCriptografado, chavePublica);
      inputStream.close();
      
      long tempoFim = System.currentTimeMillis();
      
      System.out.println("Mensagem Original: " + msgOriginal);
      System.out.println("Mensagem Criptografada: " +textoCriptografado.toString());
      System.out.println("Mensagem Decriptografada: " + textoPuro);
      
      System.out.println("O tempo de execução foi: " + (tempoFim - tempoInicio));
  
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package criptografia;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author fabiofranca
 */
public class Servidor implements Runnable {
   private static BigInteger P = new BigInteger("99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583") ;
    private static BigInteger G = new BigInteger("44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675");
       
  private Socket connection;

  private int ID;
  String Apub;
  public static void main(String[] args) {
    int port = 4567;
    int count = 0;
    try{
      ServerSocket socket1 = new ServerSocket(port);
      System.out.println("Servidor Ligado");
    
      while (true) {
        Socket connection = socket1.accept();
        Runnable runnable = new Servidor(connection, ++count);
        Thread thread = new Thread(runnable);
        thread.start();
        

      } 
    }
    catch (Exception e) {}
  }
Servidor(Socket s, int i) {
  this.connection = s;
  this.ID = i;
}
public void run() {
    try {
      
          
          // receção da chave publica enviada pelo Cliente
          
          ObjectInputStream ois_apub = new ObjectInputStream(connection.getInputStream());
          BigInteger Apublico = (BigInteger) ois_apub.readObject();
                   
          // Gerar Chave publica do Servidor
          
          GeraChaves.DiffieHellmanKey dhk = new GeraChaves.DiffieHellmanKey(P, G);
          BigInteger Bpublico = dhk.gx();
                   
          //Envio da Chave Publica 
         
          ObjectOutputStream oos_bpublico = new ObjectOutputStream(connection.getOutputStream());
          oos_bpublico.writeObject(Bpublico);
          oos_bpublico.flush();
          
          //Gerar chave secreta          
         
          dhk.key(Apublico);
                   
          //Receção do Vetor de Inicialização
          
          ObjectInputStream ois_iv = new ObjectInputStream(connection.getInputStream());          
          IvParameterSpec iv1 = new IvParameterSpec((byte[]) ois_iv.readObject());
         
         //Criação da Chave
         
          MessageDigest md = MessageDigest.getInstance("SHA-256");
          byte[] chave = md.digest(dhk.key.toByteArray());
          SecretKeySpec sk = new SecretKeySpec(chave, 0, 16, "AES");
          Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
          c.init(Cipher.DECRYPT_MODE, sk, iv1);
          
           //Receção da mensagem cifrada
         
         while (true) {
            CipherInputStream cis = new CipherInputStream(connection.getInputStream(), c);
            BufferedReader br = new BufferedReader(new InputStreamReader(cis));
            String mensagem = br.readLine();
            if (!"quit".equals(mensagem)) {
                System.out.println("O cliente " + Servidor.this.ID + " disse: " + mensagem);
            } else {
                System.out.println("O Cliente " + Servidor.this.ID + " fechou a ligação.");
                connection.close();
            }
        }
    }
    catch (Exception e) {
        
    }
}
}

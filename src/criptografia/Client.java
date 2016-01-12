/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package criptografia;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author fabiofranca
 */
public class Cliente {

    private static BigInteger P = new BigInteger("99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583");
    private static BigInteger G = new BigInteger("44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675");
    public static void main(String[] args) throws IOException {




        String host = "localhost";

        int port = 4567;
        try {

            InetAddress address = InetAddress.getByName(host);
            Socket connection = new Socket(address, port);           
            KeyGenerator.DiffieHellmanKey dhk = new KeyGenerator.DiffieHellmanKey(P, G);
            
            
            //Calculo e Envio da "chave" publica para o Servidor
            ObjectOutputStream oos_apub = new ObjectOutputStream(connection.getOutputStream());
            BigInteger Apublico = dhk.gx();  
            oos_apub.writeObject(Apublico);
            oos_apub.flush();
                        
            //Receção da chave publica enviada pelo Servidor
                      
            ObjectInputStream ois_bpublico = new ObjectInputStream(connection.getInputStream());
            BigInteger Bpublico =(BigInteger) ois_bpublico.readObject();
           
                   
            // Chave Secreta  
              dhk.key(Bpublico);
              
            // Vetor de Inicialização
              SecureRandom random = new SecureRandom();
              byte[] IV = new byte[16];
              random.nextBytes(IV);
              IvParameterSpec iv1 = new IvParameterSpec(IV);
            
            // Envio do Vetor de Inicialização  
                          
              ObjectOutputStream os = new ObjectOutputStream(connection.getOutputStream());
              os.writeObject(IV);
              os.flush();
              
              
            // Criação da Chave  
             
              MessageDigest md = MessageDigest.getInstance("SHA-256");
              byte[] chave = md.digest(dhk.key.toByteArray());
              SecretKeySpec sk = new SecretKeySpec(chave,0,16,"AES");
              
              // Encriptação  
              Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
              c.init(Cipher.ENCRYPT_MODE, sk, iv1);
            
            
              // Envio da mensagem cifrada
              String mensagem = "";
              System.out.printf("Prima Enter para enviar\nEnvie 'quit' para fechar a ligação)\n\n");
              while (connection.isConnected() == true && connection.isClosed() == false) {
                OutputStream osm = connection.getOutputStream();
                CipherOutputStream cos = new CipherOutputStream(osm, c);
                PrintWriter out = new PrintWriter(cos, true);
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Mensagem:");
                while ((mensagem = stdIn.readLine()) != null && mensagem.equals("quit") == false) {

                    out.println(mensagem);


                }
                out.println(mensagem);
                connection.close();


            }



        } catch (IOException f) {
            System.out.println("Exception" + f);
        } catch (Exception g) {
            System.out.println("Exception: " + g);
        }
    }
}

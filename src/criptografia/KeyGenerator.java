/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package criptografia;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
    
/**
 *
 * @author fabiofranca
 */
public class GeraChaves {

    public static class DiffieHellmanKey
{
  private BigInteger P;
  private BigInteger G;
  
  private BigInteger x;
  public BigInteger key;
  
  private SecureRandom rand;
  
  public DiffieHellmanKey(BigInteger P,BigInteger G)
  {
    this.P=P;
    this.G=G;
    try
    {
      this.rand=SecureRandom.getInstance("SHA1PRNG");
    }
    catch(NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
  }
  
  public BigInteger gx() throws NoSuchAlgorithmException
  {
    this.x = new BigInteger(1023, rand);

    return G.modPow(this.x, this.P);
  }
  
  public void key(BigInteger gy)
  {
    this.key=gy.modPow(this.x, this.P);
  }
}
}

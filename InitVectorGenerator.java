import java.util.Random;
abstract class InitVectorGenerator{	/* generates pseudo-random initialization vector */ 
	public static long[] getVector(){
		Random random = new Random();
		long initVector = random.nextLong();
		
		long[] iV = new long[4];
		iV[0] =  (initVector & 0xFFFF);
		iV[1] =  ((initVector >>> 16) & 0xFFFF);
		iV[2] =  ((initVector >>> 32) & 0xFFFF);
        iV[3] =  ((initVector >>> 48) & 0xFFFF);
        return iV;
	}
}
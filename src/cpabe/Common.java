package cpabe;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;


public class Common {
	public static final int FROM_ASSET = 0;
	public static final int FROM_INTERNAL = 1;
	public static final int FROM_EXTERNAL = 2;
	
	
	private static Context context;
	public static void setContext(Context mcontext){
		if(context==null)
			context=mcontext;
	}

	/* read byte[] from inputfile */
	public static byte[] suckFile(String inputfile) throws IOException {
		InputStream is = context.openFileInput(inputfile);
		int size = is.available();
		byte[] content = new byte[size];

		is.read(content);

		is.close();
		return content;
	}
	
	public static byte[] readFileFromAsset(String fileName) throws IOException{
		AssetManager assetMgr=context.getAssets();
		InputStream is = assetMgr.open(fileName);
		int size = is.available();
		byte[] content = new byte[size];
		is.read(content);
		is.close();
		return content;
	}

	/* write byte[] into outputfile */
	public static void spitFile(String outputfile, byte[] b) throws IOException {
		OutputStream os = context.openFileOutput(outputfile, Context.MODE_PRIVATE);
		os.write(b);
		os.close();
	}


	public static void writeCpabeFile(String encfile,
			byte[] cphBuf, byte[] aesBuf) throws IOException {
		int i;
		
		//Rewrite the the following for Android 
		OutputStream os = context.openFileOutput(encfile,Context.MODE_PRIVATE);
		//FileOutputStream os = openFileOutput(encfile, Context.MODE_PRIVATE);
		/* write aes_buf */
		for (i = 3; i >= 0; i--)
			os.write(((aesBuf.length & (0xff << 8 * i)) >> 8 * i));
		os.write(aesBuf);

		/* write cph_buf */
		for (i = 3; i >= 0; i--)
			os.write(((cphBuf.length & (0xff << 8 * i)) >> 8 * i));
		os.write(cphBuf);

		os.close();

	}


	public static byte[][] readCpabeFile(String encfile) throws IOException {
		int i, len;
		InputStream is = context.openFileInput(encfile);
		byte[][] res = new byte[2][];
		byte[] aesBuf, cphBuf;

		/* read aes buf */
		//TODO: clarify the length
		len = 0;
		for (i = 3; i >= 0; i--)
			len |= is.read() << (i * 8);
		aesBuf = new byte[len];

		is.read(aesBuf);

		/* read cph buf */
		len = 0;
		for (i = 3; i >= 0; i--)
			len |= is.read() << (i * 8);
		cphBuf = new byte[len];

		is.read(cphBuf);

		is.close();

		res[0] = aesBuf;
		res[1] = cphBuf;
		return res;
	}
	
	/**
	 * Return a ByteArrayOutputStream instead of writing to a file
	 */
	public static ByteArrayOutputStream writeCpabeData(byte[] mBuf,
			byte[] cphBuf, byte[] aesBuf) throws IOException {
		int i;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		/* write m_buf */
		for (i = 3; i >= 0; i--)
			os.write(((mBuf.length & (0xff << 8 * i)) >> 8 * i));
		os.write(mBuf);

		/* write aes_buf */
		for (i = 3; i >= 0; i--)
			os.write(((aesBuf.length & (0xff << 8 * i)) >> 8 * i));
		os.write(aesBuf);

		/* write cph_buf */
		for (i = 3; i >= 0; i--)
			os.write(((cphBuf.length & (0xff << 8 * i)) >> 8 * i));
		os.write(cphBuf);

		os.close();
		return os;
	}
	/**
	 * Read data from an InputStream instead of taking it from a file.
	 */
	public static byte[][] readCpabeData(InputStream is) throws IOException {
		int i, len;
		
		byte[][] res = new byte[3][];
		byte[] mBuf, aesBuf, cphBuf;

		/* read m buf */
		len = 0;
		for (i = 3; i >= 0; i--)
			len |= is.read() << (i * 8);
		mBuf = new byte[len];
		is.read(mBuf);
		
		/* read aes buf */
		len = 0;
		for (i = 3; i >= 0; i--)
			len |= is.read() << (i * 8);
		aesBuf = new byte[len];
		is.read(aesBuf);

		/* read cph buf */
		len = 0;
		for (i = 3; i >= 0; i--)
			len |= is.read() << (i * 8);
		cphBuf = new byte[len];
		is.read(cphBuf);

		is.close();
		res[0] = aesBuf;
		res[1] = cphBuf;
		res[2] = mBuf;
		return res;
	}
}

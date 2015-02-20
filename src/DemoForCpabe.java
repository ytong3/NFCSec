import cpabe.Common;
import cpabe.Cpabe;

public class DemoForCpabe {
	final static boolean DEBUG = true;

	static String pubfile = "file_dir/pub_key";
	static String mskfile = "file_dir/master_key";
	static String prvfile = "file_dir/prv_key";

	static String inputfile = "file_dir/input.pdf";
	static String encfile = "file_dir/input.pdf.cpabe";
	static String decfile = "file_dir/input.pdf.new";

	static String[] attr = { "baf1", "fim1", "foo" };
	static String policy = "foo bar fim 2of3 baf 1of2";



	static String student_attr = "objectClass:inetOrgPerson objectClass:organizationalPerson "
			+ "sn:student2 cn:student2 uid:student2 userPassword:student2 "
			+ "ou:idp o:computer mail:student2@sdu.edu.cn title:student";

	static String student_policy = "sn:student2 cn:student2 uid:student2 3of3";

	public static void main(String[] args) throws Exception {
		String attr_str;
		// attr = attr_kevin;
		// attr = attr_sara;
		// policy = policy_kevin_or_sara;
		//attr_str = array2Str(attr);

		attr_str = student_attr;
		policy = student_policy;

		Cpabe test = new Cpabe();
		println("//start to setup");
		test.setup(pubfile, mskfile);
		println("//end to setup");

		println("//start to keygen");
		test.keygen(pubfile, prvfile, mskfile, attr_str);
		println("//end to keygen");

		println("//start to enc");
		test.enc(pubfile, policy, inputfile, encfile, Common.FROM_EXTERNAL);
		println("//end to enc");

		println("//start to dec");
		test.dec(pubfile, prvfile, encfile, decfile, Common.FROM_EXTERNAL);
		println("//end to dec");
	}

	/* connect element of array with blank */
	public static String array2Str(String[] arr) {
		int len = arr.length;
		String str = arr[0];

		for (int i = 1; i < len; i++) {
			str += " ";
			str += arr[i];
		}

		return str;
	}

	private static void println(Object o) {
		if (DEBUG)
			System.out.println(o);
	}
}

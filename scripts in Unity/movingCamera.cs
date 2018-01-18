public class movingCamera : MonoBehaviour
{
	private int speed = 1;
	private string currentStr = "";
	static private string filename = "/sdcard/file.txt";
	private float x, y, z;
	private float tempX, tempY, tempZ;
	private float dX, dY, dZ;
	private float div = 5;
	private StreamReader strReader;
	private bool flag=true;
	string[] strings;


	public void Update ()
	{ 
		readFile (); 
		parsingStr ();
		move ();

	}

	public void move ()
	{ 
		tempX = 5; 
		tempY = 5; 
		tempZ = 5; 
		dX = tempX / div; 
		dY = tempY / div; 
		dZ = tempZ / div; 
		if (x < 0) { 
			while (tempX != 0) { 
				gameObject.transform.position -= gameObject.transform.right * dX; 
				tempX -= dX; 
			} 
		} else if (x > 0) { 
			while (tempX != 0) { 
				gameObject.transform.position += gameObject.transform.right * speed * Time.deltaTime; 
				tempX -= dX; 
			} 
		} 

		if (y < 0) { 
			while (tempY != 0) { 
				gameObject.transform.position -= gameObject.transform.forward * dY; 
				tempY -= dY; 
			} 
		} else if (y > 0) { 
			while (tempY != 0) { 
				gameObject.transform.position += gameObject.transform.forward * dY; 
				tempY -= dY; 
			} 
		} 

		if (z < 0) { 
			while (tempZ != 0) { 
				gameObject.transform.position -= gameObject.transform.up * dZ; 
				tempZ -= dZ; 
			} 
		} else if (z > 0) { 
			while (tempZ != 0) { 
				gameObject.transform.position += gameObject.transform.up * dZ; 
				tempZ -= dZ; 
			} 
		} 
	}
	
	private void readFile ()
	{ 
		Debug.unityLogger.Log("myLog","open readFile");
		if (File.Exists (filename)) {
			Debug.unityLogger.Log("myLog","filename exists");
			strings = File.ReadAllLines (filename);
		} else {
			Debug.unityLogger.Log("myLog","filename not exists");
		}
	}

	private void parsingStr ()
	{ 
		Debug.unityLogger.Log("myLog","open parsing");
		String[] coordinates = currentStr.Split ('/'); 
		x = Convert.ToSingle (coordinates [0]); 
		y = Convert.ToSingle (coordinates [1]); 
		z = Convert.ToSingle (coordinates [2]); 
	}

}
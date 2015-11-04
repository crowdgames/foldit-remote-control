using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class ConnectToFoldit : MonoBehaviour {

	public InputField ipAddressInput;
	public InputField requiredKeyInput;

	public void connectToFoldit() {
		Debug.Log(ipAddressInput.text);
	}
}

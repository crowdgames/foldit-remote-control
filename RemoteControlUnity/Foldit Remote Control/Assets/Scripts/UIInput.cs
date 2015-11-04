using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class UIInput : MonoBehaviour {
//	public GameObject uiContainer;
	private bool isShowing = false;
	private bool isFirstConnection = true;
	public InputField ipAddressInput;
	public InputField requiredKeyInput;
	public NetworkConScript connection;

	public void toggleUI(GameObject uiContainer) {
		if (this.gameObject.tag == "UIOptions") {
			if (isShowing) {
				this.gameObject.GetComponentInChildren<Text> ().text = "Show Options";
			} else {
				this.gameObject.GetComponentInChildren<Text> ().text = "Hide Options";
			}
		}
		isShowing = !isShowing;
		uiContainer.SetActive (isShowing);
	}

	
	public void connectToFoldit() {
		if (isFirstConnection) {

		}
		string ipAddress = ipAddressInput.text;
		string requiredKey = requiredKeyInput.text;
		connection.connect (ipAddress, requiredKey);
	}
}


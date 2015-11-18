using UnityEngine;
using UnityEngine.UI;
using System.Collections;

// Developed by Elizabeth Renn
// Developed by Ashley Sullivan

public class UIInput : MonoBehaviour {
	private bool isFirstConnection = true;
	public InputField ipAddressInput;
	public InputField requiredKeyInput;
	public NetworkConScript connection;
	

	public void toggleUI(GameObject uiContainer) {
		uiContainer.SetActive (!uiContainer.activeSelf);
	}

	// Connects to Foldit.

	public void connectToFoldit(GameObject uiContainer) {
		if (isFirstConnection) {
			GameObject closeButton = GameObject.FindGameObjectWithTag("CloseModal");
			closeButton.GetComponentInChildren<Button> ().interactable = true;
			GameObject optionsButton = GameObject.FindGameObjectWithTag("UIOptions");
			optionsButton.GetComponentInChildren<Image> ().enabled = true;
			isFirstConnection = false;
		}
		string ipAddress = ipAddressInput.text;
		string requiredKey = requiredKeyInput.text;
		connection.connect (ipAddress, requiredKey);
		uiContainer.SetActive(false);
	}

	/// 
	/// SKELETON FUNCTIONS
	/// 2SPOOKY4U UNTIL DEFINED PROPERLY


	// Re-displays connection dialog.
	public void changeIP() {

		Debug.Log ("Change IP");
	}

	// Shows/loads tutorial.
	public void showTutorial() {

		Debug.Log ("Show tutorial");
	}

	// Toggles system keyboard.
	public void toggleKeyboard() {

		Debug.Log ("Toggle keyboard");
	}


	// Adjusts the zoom % to value according to slider value.
	// (100 + slider value)%?
	public void adjustZoom() {
		// Skeleton functions.
		Debug.Log ("Adjust zoom");

	}
}


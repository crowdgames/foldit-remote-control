using UnityEngine;
using UnityEngine.UI;
using System.Collections;

// Developed by Elizabeth Renn

public class UIInput : MonoBehaviour {
	private bool isFirstConnection = true;
	public InputField ipAddressInput;
	public InputField requiredKeyInput;
	public NetworkConScript connection;

	public void toggleUI(GameObject uiContainer) {
		uiContainer.SetActive (!uiContainer.activeSelf);
	}

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
}


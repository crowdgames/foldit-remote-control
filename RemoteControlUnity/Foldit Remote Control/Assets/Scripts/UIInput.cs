using UnityEngine;
using UnityEngine.UI;
using System.Collections;

// Developed by Elizabeth Renn

public class UIInput : MonoBehaviour {
	private bool isFirstConnection = true;
	public InputField ipAddressInput;
	public InputField requiredKeyInput;
	public NetworkConScript connection;
	public Toggle lowResToggle;
	public TileRenderController tileController;
	private TouchScreenKeyboard keyboard = null;

	void Update() {
		if (keyboard != null && (keyboard.done || keyboard.wasCanceled)) {
			//don't send anything if the input was canceled
			if (!keyboard.wasCanceled) {
				Debug.Log("Said \"" + keyboard.text + "\"");
				connection.SendText(keyboard.text);
			}
			keyboard = null;
		}
	}

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
		tileController.setIfLowRes (lowResToggle.isOn);
		connection.connect (ipAddress, requiredKey);
		uiContainer.SetActive(false);
	}
	public void showKeyboard() {
		keyboard = TouchScreenKeyboard.Open("", TouchScreenKeyboardType.Default, false);
	}
}


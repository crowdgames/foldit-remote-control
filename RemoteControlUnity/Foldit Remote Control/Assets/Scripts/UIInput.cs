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

	private enum MouseButtonType : int {
		RIGHT = 0, 
		LEFT = 1,
		MIDDLE = 2,
	};
	private MouseButtonType currentButton = MouseButtonType.LEFT;
	
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
		// enable images for objects that we don't want the user to see at first
		if (isFirstConnection) {
			GameObject closeButton = GameObject.FindGameObjectWithTag("CloseModal");
			closeButton.GetComponentInChildren<Button> ().interactable = true;
			GameObject optionsButton = GameObject.FindGameObjectWithTag("UIOptions");
			optionsButton.GetComponentInChildren<Image> ().enabled = true;
			GameObject menuArrow = GameObject.FindGameObjectWithTag("MenuArrow");
			menuArrow.GetComponentInChildren<Image> ().enabled = true;
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

	public void switchToRightClick() {
		if (currentButton != MouseButtonType.RIGHT) {
			// send the previous key up message unless we switched from the left click
			if (currentButton == MouseButtonType.MIDDLE) {
				connection.ModKey(10, 10, false, NetworkConScript.keys.Shift);
			}

			// send the key down message
			connection.ModKey(10, 10, true, NetworkConScript.keys.Ctrl);
				
			currentButton = MouseButtonType.RIGHT;
			Debug.Log ("now right clicking");
		}
	}

	public void switchToLeftClick() {
		if (currentButton != MouseButtonType.LEFT) {

			// send the previous key up message
			if (currentButton == MouseButtonType.RIGHT) {
				connection.ModKey(10, 10, false, NetworkConScript.keys.Ctrl);
			} else {
				connection.ModKey(10, 10, false, NetworkConScript.keys.Shift);
			}

			currentButton = MouseButtonType.LEFT;
			Debug.Log ("now left clicking");
		}
	}

	public void switchToMiddleClick() {
		if (currentButton != MouseButtonType.MIDDLE) {
			// send the previous key up message unless we switched from the left click
			if (currentButton == MouseButtonType.RIGHT) {
				connection.ModKey(10, 10, false, NetworkConScript.keys.Ctrl);
			}
			
			// send the key down message
			connection.ModKey(10, 10, true, NetworkConScript.keys.Shift);
			currentButton = MouseButtonType.MIDDLE;
			Debug.Log ("now middle clicking");
		}
	}
}


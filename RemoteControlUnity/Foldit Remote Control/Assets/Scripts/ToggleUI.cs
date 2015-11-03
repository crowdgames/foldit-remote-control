using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class ToggleUI : MonoBehaviour {
	public GameObject uiContainer;
	private bool isShowing = false;

	public void toggleUI() {
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
}


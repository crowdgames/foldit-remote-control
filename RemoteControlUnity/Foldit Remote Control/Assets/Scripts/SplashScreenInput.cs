using UnityEngine;
using System.Collections;


// Written by Elizabeth
public class SplashScreenInput : MonoBehaviour {
	public GameObject helpWindow;

	public void startRemoteControl() {
		Application.LoadLevel ("FolditRemoteControl"); 
	}

	public void showHelpWindow() {
		helpWindow.SetActive (true);
	}
	
	public void hideHelpWindow() {
		helpWindow.SetActive (false);
	}
}

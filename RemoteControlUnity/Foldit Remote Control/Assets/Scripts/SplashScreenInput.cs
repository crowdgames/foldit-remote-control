using UnityEngine;
using System.Collections;
using UnityEngine.SceneManagement;


// Written by Elizabeth
public class SplashScreenInput : MonoBehaviour {
	public GameObject helpWindow;

	public void startRemoteControl() {
		SceneManager.LoadScene("FolditRemoteControl", LoadSceneMode.Single);
	}

	public void showHelpWindow() {
		helpWindow.SetActive (true);
	}
	
	public void hideHelpWindow() {
		helpWindow.SetActive (false);
	}
}

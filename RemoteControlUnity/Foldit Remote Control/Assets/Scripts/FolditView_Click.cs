using UnityEngine;
using System.Collections;
using TouchScript.Gestures;
using System;
using UnityEngine.UI;
using UnityEngine.EventSystems;

// Ashley Sullivan

// Clicking in the Foldit view window behavior.

public class FolditView_Click : MonoBehaviour, IPointerClickHandler {
	// Drag to initialize
	public GameObject networkCon;
	// Use this for initialization
	void Start () {
		GetComponent<TapGesture>().Tapped += clickInFoldit;

	}
	
	// Update is called once per frame
	void Update () {
	
	}



	/// <summary>
	/// React to clicks.
	/// </summary>
	public virtual void OnPointerClick(PointerEventData eventData)
	{
//		 
		Vector2 localpos = Input.mousePosition;
		Debug.Log ("Local point: " + localpos);
		networkCon.GetComponent<NetworkConScript>().Tap(true, 
		                                                Mathf.Floor(localpos.x), 
		                                                Mathf.Floor(localpos.y));
		networkCon.GetComponent<NetworkConScript>().Tap(false, 
		                                                Mathf.Floor(localpos.x), 
		                                                Mathf.Floor(localpos.y));
	}

	// Sends a click for the correct coordinates to Foldit
	public void clickInFoldit(object sender, EventArgs e)
	{
		Vector2 localpos = (sender as TapGesture).ScreenPosition;
		Debug.Log ("Local point: " + localpos);
		networkCon.GetComponent<NetworkConScript>().Tap(true, 
		                                                Mathf.Floor(localpos.x), 
		                                                Mathf.Floor(localpos.y));
		networkCon.GetComponent<NetworkConScript>().Tap(false, 
		                                                Mathf.Floor(localpos.x), 
		                                                Mathf.Floor(localpos.y));
	}

}


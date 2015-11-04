using UnityEngine;
using System.Collections;
using TouchScript.Gestures;
using UnityEngine.UI;
using System;

public class btnClick : MonoBehaviour {

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	private void OnEnable()
	{
		// subscribe to gesture's Tapped event
		GetComponent<TapGesture>().Tapped += tappedHandler;
	}
	
	private void OnDisable()
	{
		// don't forget to unsubscribe
		GetComponent<TapGesture>().Tapped -= tappedHandler;
	}

	private void tappedHandler(object sender, EventArgs e)
	{
		Button b = GetComponent<Button>(); 
		ColorBlock cb = b.colors;
		cb.normalColor = Color.red;
		b.colors = cb;
	}
}

using UnityEngine;
using System.Collections;
using TouchScript.Gestures;
using System;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using TouchScript.Gestures.Simple;

// Ashley Sullivan
// Sean Moss

// Clicking in the Foldit view window behavior.

public class FolditView_Click : MonoBehaviour, IPointerClickHandler, IPointerDownHandler, IPointerUpHandler, IDragHandler
{
	// Drag to initialize
	public GameObject networkCon;
	// Use this for initialization
	void Start () {
		GetComponent<TapGesture>().Tapped += clickInFoldit;
        //GetComponent<PressGesture>().Pressed += pressedHandler;
        //GetComponent<SimplePanGesture>().Panned += panHandler;
        //GetComponent<ReleaseGesture>().Released += releasedHandler;

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
		                                                Mathf.FloorToInt(localpos.x), 
		                                                Mathf.FloorToInt(localpos.y));
		networkCon.GetComponent<NetworkConScript>().Tap(false, 
		                                                Mathf.FloorToInt(localpos.x), 
		                                                Mathf.FloorToInt(localpos.y));
	}

	// Sends a click for the correct coordinates to Foldit
	public void clickInFoldit(object sender, EventArgs e)
	{
		Vector2 localpos = (sender as TapGesture).ScreenPosition;
		Debug.Log ("Local point: " + localpos);
		
		networkCon.GetComponent<NetworkConScript>().Tap(true, 
		                                                Mathf.FloorToInt(localpos.x), 
		                                                Mathf.FloorToInt(localpos.y));
		networkCon.GetComponent<NetworkConScript>().Tap(false, 
		                                                Mathf.FloorToInt(localpos.x), 
		                                                Mathf.FloorToInt(localpos.y));
	}


    private void releasedHandler(object sender, EventArgs e)
    {
        networkCon.GetComponent<NetworkConScript>().Tap(false, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    private void pressedHandler(object sender, EventArgs e)
    {
        networkCon.GetComponent<NetworkConScript>().Tap(true, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    private void panHandler(object sender, EventArgs e)
    {
        networkCon.GetComponent<NetworkConScript>().MouseMove((int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    public void OnPointerDown(PointerEventData eventData)
    {
        networkCon.GetComponent<NetworkConScript>().Tap(true, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    public void OnPointerUp(PointerEventData eventData)
    {
        networkCon.GetComponent<NetworkConScript>().Tap(false, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    public void OnDrag(PointerEventData eventData)
    {
        networkCon.GetComponent<NetworkConScript>().MouseMove((int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }
}


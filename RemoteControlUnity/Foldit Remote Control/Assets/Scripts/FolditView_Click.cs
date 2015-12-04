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
    private NetworkConScript netConScript;

	// Use this for initialization
	void Start () {
        netConScript = networkCon.GetComponent<NetworkConScript>();
        GetComponent<TapGesture>().Tapped += clickInFoldit;
        GetComponent<SimplePanGesture>().Panned += panHandler;
        GetComponent<ReleaseGesture>().Released += releasedHandler;
        GetComponent<PressGesture>().Pressed += pressedHandler;
    }

    // Update is called once per frame
    void Update () {
	
	}



	/// <summary>
	/// React to clicks.
	/// </summary>
	public virtual void OnPointerClick(PointerEventData eventData)
	{
	}

	// Sends a click for the correct coordinates to Foldit
	public void clickInFoldit(object sender, EventArgs e)
	{
	}

    private void releasedHandler(object sender, EventArgs e)
    {
        netConScript.Tap(false, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    private void pressedHandler(object sender, EventArgs e)
    {
        netConScript.Tap(true, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    private void panHandler(object sender, EventArgs e)
    {
        netConScript.MouseMove((int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    public void OnPointerDown(PointerEventData eventData)
    {
        netConScript.Tap(true, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    public void OnPointerUp(PointerEventData eventData)
    {
        netConScript.Tap(false, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    public void OnDrag(PointerEventData eventData)
    {
        netConScript.MouseMove((int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }
}


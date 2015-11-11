using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;

// Developed by Courtney Toder and Elizabeth Renn

public class DragMenu : MonoBehaviour, IBeginDragHandler, IDragHandler, IEndDragHandler {
	public static GameObject dragged;
	Vector3 startPosition;
	private float lockYPosition = Screen.height / 2f;
	private const float lockZPosition = 0f;

	#region IBeginDragHandler implementation

	public void OnBeginDrag (PointerEventData eventData)
	{
		dragged = gameObject;
		startPosition = transform.position;
	}

	#endregion

	#region IDragHandler implementation

	public void OnDrag (PointerEventData eventData)
	{
		RectTransform rt = (RectTransform)dragged.transform;
		float maxX = Screen.width * .39f;
		float minPositionX = Screen.width * .05f;
		float minDragX = Screen.width * .15f;
		if (Input.mousePosition.x > maxX) {
			startPosition = new Vector3 (maxX, lockYPosition, lockZPosition);
			transform.position = new Vector3 (maxX, lockYPosition, lockZPosition);
		} else if (Input.mousePosition.x < minDragX) {
			startPosition = new Vector3 (minPositionX, lockYPosition, lockZPosition);
			transform.position = new Vector3 (Input.mousePosition.x, lockYPosition, lockZPosition);
		} else {
			transform.position = new Vector3 (Input.mousePosition.x, lockYPosition, lockZPosition);
		}
	}

	#endregion

	#region IEndDragHandler implementation
	public void OnEndDrag (PointerEventData eventData)
	{
		dragged = null;
		transform.position = startPosition;
	}
	#endregion
}
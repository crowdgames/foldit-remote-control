using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;

// Developed by Courtney Toder and Elizabeth Renn

public class DragMenu : MonoBehaviour, IDragHandler, IEndDragHandler {
	private const float minAnchorXPulledOut = .7f;
	private const float minAnchorXPulledIn = .958f;
	private const float maxAnchorXPulledOut = 1.0f;
	private const float maxAnchorXPulledIn = 1.258f;

	#region IDragHandler implementation

	public void OnDrag (PointerEventData eventData)
	{
		RectTransform trans = (RectTransform)transform;
		float minX = minAnchorXPulledOut * Screen.width;
		float maxX = minAnchorXPulledIn * Screen.width;
		float percentage = (Input.mousePosition.x - minX) / (maxX - minX);

		trans.anchorMin = new Vector2 (Mathf.Lerp (minAnchorXPulledOut, minAnchorXPulledIn, percentage), 0);
		trans.anchorMax = new Vector2 (Mathf.Lerp (maxAnchorXPulledOut, maxAnchorXPulledIn, percentage), 1);
	}

	#endregion

	#region IEndDragHandler implementation
	public void OnEndDrag (PointerEventData eventData)
	{
		// Snap to the closest side if we aren't already there.
		RectTransform trans = (RectTransform)transform;
		float minX = minAnchorXPulledOut * Screen.width;
		float maxX = minAnchorXPulledIn * Screen.width;
		float percentage = (Input.mousePosition.x - minX) / (maxX - minX);

		// if the percentage is within 50% snap to the left, otherwise snap to the right
		if (percentage <= .5) {
			trans.anchorMin = new Vector2 (minAnchorXPulledOut, 0);
			trans.anchorMax = new Vector2 (maxAnchorXPulledOut, 1);
		} else {
			trans.anchorMin = new Vector2 (minAnchorXPulledIn, 0);
			trans.anchorMax = new Vector2 (maxAnchorXPulledIn, 1);
		}
	}
	#endregion
}
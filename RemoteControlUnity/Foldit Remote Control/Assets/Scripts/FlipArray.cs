using UnityEngine;
using System.Collections;

public class FlipArray {

	public static Color32[] Flip(Color32[] colors, int width, int height)
    {
        int count = width * height;
        Color32[] newColors = new Color32[count];

        for(int i = 0; i < count; i++)
        {
            int x = i % width;
            int y = i / width;
            int newY = height - y - 1;
            newColors[x + (newY * width)] = colors[i];
        }
        return newColors;
    }
}

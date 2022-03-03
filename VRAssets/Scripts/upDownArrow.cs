using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class upDownArrow : MonoBehaviour
{
    bool down = false;
    bool up = false;
    // Start is called before the first frame update
    void Start()
    {
        up = true;
        InvokeRepeating("SlowUpdate", 0.0f, 0.05f);
    }

    // Update is called once per frame

    void Update()
    {


    }
    void SlowUpdate()
    {
        if (up && gameObject.transform.position.y < 1.29)
        {
            gameObject.transform.position = gameObject.transform.position + new Vector3(0.0f, 0.0025f, 0.0f);
        }
        else
        {
            up = false;
            down = true;
        }
        if (down && gameObject.transform.position.y > 1.19)
        {
            gameObject.transform.position = gameObject.transform.position - new Vector3(0.0f, 0.0025f, 0.0f);
        }
        else
        {
            up = true;
            down = false;
        }
    }
}

package com.example.squashieproject

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import io.socket.client.Socket
import com.example.squashieproject.MainActivity.Companion
import kotlinx.android.synthetic.main.fragment_home.*
import java.security.KeyStore
import java.util.jar.Attributes

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var socket: Socket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var fragmentView = inflater.inflate(R.layout.fragment_home, container, false) //Inflate the layout for this fragment and get view of this fragment
        var textViewPlayingSong = fragmentView.findViewById<TextView>(R.id.textViewPlayingSong) //get textview inside fragmentView
        var logo = fragmentView.findViewById<ImageView>(R.id.imageView) //get imageView contains logo inside fragmentView
        var mainActivity = activity //get the parent activity
        var socket:Socket = MainActivity.socket //get companion val of "socket"
        var rotation = AnimationUtils.loadAnimation(mainActivity, R.anim.rotate) //load animation on parent activity
        rotation.interpolator = LinearInterpolator() //set interpolator of animation to linear interpolator for persistent animation speed
        mainActivity?.runOnUiThread {
            textViewPlayingSong.isSelected = true //enable textview sliding if text is too long
        }
        //once "connect" event fired
        socket.once("connect",{
            socket.emit("sync","request to get current song") //client socket fires "sync" to get current song playing
        })

        //This is to start animation once server fires "respond to sync" event
        socket.once("respond to sync",{
            mainActivity?.runOnUiThread {
                rotation.fillAfter = true
                logo.startAnimation(rotation)
            }
        })

        //This is whenever socket fires "respond to sync"
        socket.on("respond to sync",{
            mainActivity?.runOnUiThread {
                textViewPlayingSong.text = "Playing : " + it[0].toString() //extract name of current song sent from "respond to sync" and push it on textview
            }
        })

        //If socket is not connected
        if(!socket.connected())
            socket.connect()

        return fragmentView
    }

    companion object {

    }
}

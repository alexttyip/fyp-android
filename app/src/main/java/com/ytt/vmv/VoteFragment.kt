package com.ytt.vmv

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import kotlin.math.min

private var selected = -1
private val data = Array(4) {
    Candidate(
            "$it",
            "https://picsum.photos/id/${it * 10}/200"
    )
}

class VoteFragment : Fragment(), View.OnClickListener {

    private lateinit var linearLayout: LinearLayout

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vote, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            Log.e("FAB", "CLICKED!")
            Log.e("Selected", "$selected")
            if (selected != -1) {
                AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Vote")
                        .setMessage("You voted for: ${data[selected].name}\n\nDo you wish to proceed?")
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            Snackbar.make(it!!, "You voted for ${data[selected].name}", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
            }
        }

        linearLayout = view.findViewById(R.id.list_choices)

        data.forEachIndexed { index, (name, picUrl) ->
            val cardView = layoutInflater.inflate(R.layout.list_card_item, linearLayout, false) as ConstraintLayout

            val textView = cardView.findViewById<TextView>(R.id.text1)
            textView.text = name

            if (selected == index) {
                textView.setTextColor(Color.CYAN)
            } else {
                textView.setTextColor(Color.BLACK)
            }

            Picasso.get()
                    .load(picUrl)
                    .resize(60, 60)
                    .centerCrop()
                    .transform(CircleTransform())
                    .into(cardView.findViewById<ImageView>(R.id.image_profile_pic))

            cardView.setOnClickListener(this)

            linearLayout.addView(cardView)
        }
    }

    private fun setCardColor(parent: LinearLayout, i: Int, color: Int) {
        (parent.getChildAt(i) as ConstraintLayout).getChildAt(0)
                .setBackgroundColor(color)
    }

    override fun onClick(v: View?) {
        if (!this::linearLayout.isInitialized) return

        val i = linearLayout.indexOfChild(v)

        if (i == -1 || i == selected) return

        if (selected != -1)
            setCardColor(linearLayout, selected, Color.WHITE)

        setCardColor(linearLayout, i, Color.CYAN)

        selected = i
    }
}

data class Candidate(val name: String, val picUrl: String)

class CircleTransform : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(size, size, source.config)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}
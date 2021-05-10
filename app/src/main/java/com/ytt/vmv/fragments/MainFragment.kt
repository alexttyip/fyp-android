package com.ytt.vmv.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ytt.vmv.R
import com.ytt.vmv.databinding.FragmentMainBinding
import com.ytt.vmv.databinding.ListTwoLinesItemBinding
import com.ytt.vmv.models.ElectionModel
import java.util.*

private val mockData = List(10) {
    ElectionModel(
        "UK General Election 200$it",
        Date()
    )
}

class MainFragment : Fragment() {
    private var fragmentMainBinding: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        fragmentMainBinding = binding

        binding.recycler.also {
            it.adapter = ElectionAdapter(mockData)
            it.layoutManager = LinearLayoutManager(context)
        }

        binding.fab.setOnClickListener {
            Log.e("FAB", "Clicked")
            findNavController()
                .navigate(R.id.action_mainFragment_to_voteFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        fragmentMainBinding = null
        super.onDestroyView()
    }

    class ElectionAdapter(private val data: List<ElectionModel>) :
        RecyclerView.Adapter<ElectionAdapter.MyViewHolder>() {
        class MyViewHolder(val binding: ListTwoLinesItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(model: ElectionModel) {
                Log.e("Bind", model.name)
                binding.model = model
                binding.executePendingBindings()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListTwoLinesItemBinding.inflate(inflater, parent, false)

            return MyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val election = data[position]
            holder.bind(election)
        }

        override fun getItemCount() = data.size
    }
}
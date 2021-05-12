package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
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
        Date().time
    )
}

class MainFragment : Fragment(), ElectionItemClickListener {
    private var fragmentMainBinding: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        fragmentMainBinding = binding

        val myLayoutManager = LinearLayoutManager(context)
        val decoration = DividerItemDecoration(context, myLayoutManager.orientation)

        binding.recycler.also {
            it.adapter = ElectionAdapter(mockData, this)
            it.layoutManager = myLayoutManager
            it.addItemDecoration(decoration)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onDestroyView() {
        fragmentMainBinding = null
        super.onDestroyView()
    }

    class ElectionAdapter(
        private val data: List<ElectionModel>,
        private val itemClickListener: ElectionItemClickListener
    ) :
        RecyclerView.Adapter<ElectionAdapter.MyViewHolder>() {
        class MyViewHolder(private val binding: ListTwoLinesItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(model: ElectionModel, itemClickListener: ElectionItemClickListener) {
                binding.model = model
                binding.itemClickListener = itemClickListener
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
            holder.bind(election, itemClickListener)
        }

        override fun getItemCount() = data.size
    }

    override fun onItemClick(model: ElectionModel) {
        findNavController()
            .navigate(MainFragmentDirections.actionMainFragmentToVoteFragment(model.name, model))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
                || super.onOptionsItemSelected(item)
    }

}

fun interface ElectionItemClickListener {
    fun onItemClick(model: ElectionModel)
}

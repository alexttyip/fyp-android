package com.ytt.vmv.fragments

import androidx.fragment.app.Fragment

class KeyListFragment : Fragment() {

}
// TODO
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.recyclerview.widget.*
//import com.ytt.vmv.VMVApplication
//import com.ytt.vmv.database.entities.KeyPairs
//import com.ytt.vmv.databinding.FragmentKeyListBinding
//import com.ytt.vmv.databinding.ListTwoLinesItemBinding
//import com.ytt.vmv.models.KeyPairsViewModel
//import com.ytt.vmv.models.KeyPairsViewModelFactory
//
//class KeyListFragment : Fragment(), KeyPairsItemClickListener {
//    private var fragmentKeyListBinding: FragmentKeyListBinding? = null
//
//    private val keyPairsViewModel: KeyPairsViewModel by viewModels {
//        KeyPairsViewModelFactory((requireActivity().application as VMVApplication).keyPairsRepository)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentKeyListBinding.inflate(inflater, container, false)
//        fragmentKeyListBinding = binding
//
//        val adapter = KeyPairsListAdapter(this)
//        val myLayoutManager = LinearLayoutManager(context)
//        val decoration = DividerItemDecoration(context, myLayoutManager.orientation)
//
//        binding.recycler.also {
//            it.adapter = adapter
//            it.layoutManager = myLayoutManager
//            it.addItemDecoration(decoration)
//        }
//
//        keyPairsViewModel.allKeyPairs.observe(viewLifecycleOwner) { keyPairs ->
//            keyPairs?.let { adapter.submitList(it) }
//        }
//
//        setHasOptionsMenu(true)
//
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        fragmentKeyListBinding = null
//        super.onDestroyView()
//    }
//
//    class KeyPairsListAdapter(
//        private val itemClickListener: KeyPairsItemClickListener
//    ) :
//        ListAdapter<KeyPairs, KeyPairsListAdapter.KeyPairsViewHolder>(KeyPairssComparator()) {
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyPairsViewHolder {
//            return KeyPairsViewHolder.create(parent)
//        }
//
//        override fun onBindViewHolder(holder: KeyPairsViewHolder, position: Int) {
//            holder.bind(getItem(position), itemClickListener)
//        }
//
//        class KeyPairsViewHolder(private val binding: ListTwoLinesItemBinding) :
//            RecyclerView.ViewHolder(binding.root) {
//
//            @SuppressLint("SetTextI18n")
//            fun bind(keyPairs: KeyPairs, itemClickListener: KeyPairsItemClickListener) {
//                binding.line1.text = keyPairs.name
//                binding.line2.text = keyPairs.signingPublicKey.toString()
//                binding.root.setOnClickListener {
//                    itemClickListener.onItemClick(keyPairs)
//                }
//            }
//
//            companion object {
//                fun create(parent: ViewGroup): KeyPairsViewHolder {
//                    val binding =
//                        ListTwoLinesItemBinding.inflate(
//                            LayoutInflater.from(parent.context),
//                            parent,
//                            false
//                        )
//                    return KeyPairsViewHolder(binding)
//                }
//            }
//        }
//
//        class KeyPairssComparator : DiffUtil.ItemCallback<KeyPairs>() {
//            override fun areItemsTheSame(oldItem: KeyPairs, newItem: KeyPairs): Boolean {
//                return oldItem === newItem
//            }
//
//            override fun areContentsTheSame(oldItem: KeyPairs, newItem: KeyPairs): Boolean {
//                return oldItem.name == newItem.name
//            }
//        }
//    }
//
//
//    override fun onItemClick(keyPairs: KeyPairs) {
//        // TODO to key details
//
//        Log.e("Clicked", keyPairs.name)
////        findNavController()
////            .navigate(MainFragmentDirections.actionMainFragmentToVoteFragment(model.name, model))
//    }
//}
//
//fun interface KeyPairsItemClickListener {
//    fun onItemClick(keyPairs: KeyPairs)
//}

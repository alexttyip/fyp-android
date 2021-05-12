package com.ytt.vmv.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ytt.vmv.cryptography.Parameters
import com.ytt.vmv.databinding.FragmentKeyListBinding
import com.ytt.vmv.databinding.ListTwoLinesItemBinding
import com.ytt.vmv.models.ElectionModel
import com.ytt.vmv.models.KeyModel
import java.math.BigInteger
import java.util.*

private val mockParams = Parameters(
    BigInteger("2598057200191543995471923089764020105117731191522555614205853287038989965374225279933868752527022358332786524355459758106280271543191716570203864654424960790071179773765038522538833812955303186745650353437677418779388208308211023934621778311706814333772989629292949867092472825902375695491209115050685821991120019725288538507191126610371800735991516641826245082502429298352505531947571237454545780629922932126865383479483820239665281727418611532412492102451321520674943061216619542096946940447510373814109472584964849324672948752069693753037554398178550350902065734698715757683026278288302445234840193261512469799196344377581963878840207317998678413416021173971922444074254764716508309510377344452500883110875903959269552184509104526356317071120513048839731245637130698526765580505227949869475862521725676848262785336131977356983050479986532231481625310836703522767441380844958515095382775540300441364634294081448417813760012"),
    BigInteger("5124895261302837619800251387588824849628751704286808668257495951678009002982328956144066796771230672491077647173090883618783781789277425731855323585662333763460698783592497327193750252279841319803514412692474440041331597736096913649716557367827757536593961355439830373314593072375890907015996951475299892070984332539515833288932214144979433880622419929837243394764619469372906533569333906633325033683863986391193041928110237850983849437768745753735332064428596060041390948404871496993051962626268710600338389683466934156307961988466428182972142991654354391344299716891626878201512680402599876997482278139409166115707683682782550136726118824552126694061804531242067394833201113178395604516023415398379463560810727049177689693115023078745225526237287696671629631460901780583211628317338944039290945903668057569639148031104536044598928731787086667082444023526605722248992144644626624546923077244255202744375039716468722812089549"),
    BigInteger("115504398497635136294544250486766651828766392199363432662071947814745255935669")
)

private val mockData = List(10) {
    KeyModel.generateKeys(
        ElectionModel(
            "UK General Election 200$it",
            Date().time
        ),
        mockParams
    )
}

class KeyListFragment : Fragment(), KeyListItemClickListener {
    private var fragmentKeyListBinding: FragmentKeyListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentKeyListBinding.inflate(inflater, container, false)
        fragmentKeyListBinding = binding

        val myLayoutManager = LinearLayoutManager(context)
        val decoration = DividerItemDecoration(context, myLayoutManager.orientation)

        binding.recycler.also {
            it.adapter = KeyListAdapter(mockData, this)
            it.layoutManager = myLayoutManager
            it.addItemDecoration(decoration)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onDestroyView() {
        fragmentKeyListBinding = null
        super.onDestroyView()
    }

    class KeyListAdapter(
        private val data: List<KeyModel>,
        private val itemClickListener: KeyListItemClickListener
    ) :
        RecyclerView.Adapter<KeyListAdapter.MyViewHolder>() {
        class MyViewHolder(private val binding: ListTwoLinesItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(model: KeyModel, itemClickListener: KeyListItemClickListener) {
                binding.line1.text = model.election.name
                binding.line2.text = model.trapdoor.publicKey.toString()
                binding.root.setOnClickListener {
                    itemClickListener.onItemClick(model)
                }
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

    override fun onItemClick(model: KeyModel) {
        // TODO to key details
//        findNavController()
//            .navigate(MainFragmentDirections.actionMainFragmentToVoteFragment(model.name, model))
    }
}

fun interface KeyListItemClickListener {
    fun onItemClick(model: KeyModel)
}

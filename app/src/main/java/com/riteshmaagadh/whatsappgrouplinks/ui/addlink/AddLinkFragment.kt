package com.riteshmaagadh.whatsappgrouplinks.ui.addlink

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.riteshmaagadh.whatsappgrouplinks.R
import com.riteshmaagadh.whatsappgrouplinks.data.models.Group
import com.riteshmaagadh.whatsappgrouplinks.databinding.FragmentAddLinkBinding
import com.riteshmaagadh.whatsappgrouplinks.ui.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class AddLinkFragment : Fragment() {

    private lateinit var binding: FragmentAddLinkBinding
    private var imageUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddLinkBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addGroupBtn.setOnClickListener {
            if (Utils.isOnline(requireContext())){
                if (binding.groupNameEditText.text!!.isNotEmpty()){
                    if (binding.groupLinkEditText.text!!.isNotEmpty()){
                        addGroupRequest(
                            binding.groupNameEditText.text.toString(),
                            binding.groupLinkEditText.text.toString(),
                            binding.categoriesSpinner.selectedItem.toString()
                        )
                    } else {
                        binding.groupLinkInputLayout.error = "Group link is required!"
                    }
                } else {
                    binding.groupNameInputLayout.error = "Group name is required!"
                }
            } else {
                Snackbar.make(requireContext(),binding.root,"No Internet!", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1001)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, dataIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataIntent)
        var path: Uri? = null
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            if (dataIntent == null || dataIntent.data == null) {
                return
            }
            path = dataIntent.data
            try {
                if (path != null) {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, path)
                    Glide.with(requireContext())
                        .load(bitmap)
                        .placeholder(R.drawable.picture)
                        .into(binding.imageView)
                    val boas = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20,boas)
                    val ref = FirebaseStorage.getInstance()
                        .reference.child("WhatsAppGroup" + System.currentTimeMillis() + ".jpg")
                    ref.putBytes(boas.toByteArray())
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener {
                                imageUrl = it.toString()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(),"Failed to upload image! Try Again.",Toast.LENGTH_SHORT).show()
                        }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun addGroupRequest(title: String, groupLink: String, category: String) {
        val group = Group(title, groupLink, category,imageUrl)
        lifecycleScope.launch(Dispatchers.IO){
            FirebaseFirestore.getInstance()
                .collection("whatsapp_groups")
                .add(group)
                .addOnSuccessListener {
                    showSuccessDialog()
                }
                .addOnFailureListener {
                    Snackbar.make(requireContext(),binding.root,"Failed. Try Again!", Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    private fun showSuccessDialog(){
        val layoutInflater = LayoutInflater.from(requireContext());
        val view = layoutInflater.inflate(R.layout.added_confirmation_dialog, null)

        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()
        alertDialog.setView(view)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val yesAction: Button = view.findViewById(R.id.yesButton)

        yesAction.setOnClickListener {
            binding.groupNameEditText.setText("")
            binding.groupLinkEditText.setText("")
            binding.groupLinkInputLayout.error = null
            binding.groupNameInputLayout.error = null
            alertDialog.dismiss()
        }


    }


    companion object {
        @JvmStatic
        fun newInstance() =
            AddLinkFragment()
    }
}
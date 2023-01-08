package com.hameed.danish
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hameed.danish.domain.Order
import com.hameed.danish.domain.Product
import com.hameed.danish.domain.User
import com.hameed.danish.ui.ProductAdapter
import com.hameed.danish.viewmodels.DanishApiStatus
import java.io.File
import java.io.FileOutputStream
import java.util.Objects

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let{
        val imgUri = imgUrl.toUri().buildUpon().scheme("http").build()
        imgView.load(imgUri){
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }

}
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<Product>?) {
    val adapter = recyclerView.adapter as ProductAdapter
    adapter.submitList(data)
}
@BindingAdapter("usersListData")
fun bindUserRecyclerView(recyclerView: RecyclerView,
                     data: List<User>?) {
    val adapter = recyclerView.adapter as UserAdapter
    adapter.submitList(data)
}
@BindingAdapter("ordersListData")
fun bindOrderRecyclerView(recyclerView: RecyclerView,
                         data: List<Order>?) {
    val adapter = recyclerView.adapter as OrderAdapter
    adapter.submitList(data)
}
@BindingAdapter("marsApiStatus")
fun bindStatus(statusImageView: ImageView,
               status: DanishApiStatus?) {

    when (status) {
        DanishApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        DanishApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        DanishApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }

        else -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
    }

}

/*
@BindingAdapter("downloadImage")
fun downloadImage(statusImageView: ImageView,
product: Product) {

statusImageView.setOnClickListener{
val bitmapDrawable:BitmapDrawable = statusImageView.drawable as BitmapDrawable
val bitmap = bitmapDrawable.bitmap
var outputStream: FileOutputStream? = null
val file = Environment.getExternalStorageDirectory()
val dir = File(file.absolutePath+"/Danish")
dir.mkdir()
val fileName = String.format("%d.png",System.currentTimeMillis())
val  outputFile = File(dir,fileName)
try {
outputStream = FileOutputStream(outputFile)
}
catch (e:Exception)
{
Log.e("download",e.message.toString())

}
bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
try {
outputStream?.flush()
}
catch (e:Exception)
{
Log.e("download",e.message.toString())

}
try {
outputStream?.close()
}
catch (e:Exception)
{
Log.e("download",e.message.toString())

}
Log.e("download","Done")
}
}
*/

@BindingAdapter("shareImage")
fun ImageView.shareImage(
                  name:String) {

    this.setOnClickListener{

       try
       {
           val bitmapDrawable:BitmapDrawable  =  this.drawable as BitmapDrawable
           val bitmap:Bitmap  = bitmapDrawable.bitmap
           shareimg(bitmap,context,name)
       }
       catch (_:Exception)
       {

       }

    }
}

fun shareimg(bitmap: Bitmap,context: Context,name: String) {
    val share = Intent(Intent.ACTION_SEND)
    share.type = "image/*"
    val uri:Uri = saveImages(bitmap,context)
    share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    share.putExtra(Intent.EXTRA_STREAM,uri)
    share.putExtra(Intent.EXTRA_SUBJECT,"Danish")
    share.putExtra(Intent.EXTRA_TEXT, name)
    context.startActivity(Intent.createChooser(share,"Share image.."))
}

fun saveImages(bitmap: Bitmap, context: Context): Uri {
    val imagesFolder = File(context.cacheDir,"images")
    imagesFolder.mkdir()
    val file = File(imagesFolder,"shared images.jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
    outputStream.flush()
    outputStream.close()
    return FileProvider.getUriForFile(Objects.requireNonNull(context.applicationContext),
    "com.hameed.danish"+".provider",file)

}





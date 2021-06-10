
import java.awt.image.BufferedImage
import javax.imageio.ImageIO



private var icon: BufferedImage? = null
fun icAppRounded(): BufferedImage {
    if (icon != null) {
        return icon!!
    }
    try {
        val imageRes = "images/ic_imageviewer_round.png"
        val img = Thread.currentThread().contextClassLoader.getResource(imageRes)
        val bitmap: BufferedImage? = ImageIO.read(img)
        if (bitmap != null) {
            icon = bitmap
            return bitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
}

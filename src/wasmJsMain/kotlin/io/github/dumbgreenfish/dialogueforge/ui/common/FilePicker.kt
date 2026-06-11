package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader

@JsFun("(arr, i) => arr[i]")
private external fun int8At(arr: Int8Array, i: Int): Int

@Composable
actual fun rememberFilePicker(accept: List<String>, onResult: (ByteArray, String) -> Unit): () -> Unit =
    remember(accept) {
        {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = accept.joinToString(",")
            input.addEventListener("change") {
                val file = input.files?.item(0) ?: return@addEventListener
                val reader = FileReader()
                reader.addEventListener("load") {
                    val buffer = reader.result as ArrayBuffer
                    val int8 = Int8Array(buffer)
                    onResult(ByteArray(int8.length) { i -> int8At(int8, i).toByte() }, file.name)
                }
                reader.readAsArrayBuffer(file)
            }
            input.click()
        }
    }

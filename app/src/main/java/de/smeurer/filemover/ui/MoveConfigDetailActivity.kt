package de.smeurer.filemover.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.abdeveloper.library.MultiSelectDialog
import com.abdeveloper.library.MultiSelectModel
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import de.smeurer.filemover.R
import de.smeurer.filemover.data.Mode
import de.smeurer.filemover.data.MoveConfig
import kotlinx.android.synthetic.main.activity_config_detail.*
import org.jetbrains.anko.design.snackbar
import java.io.File


class MoveConfigDetailActivity : AbstractBaseActivity() {

    private var fromFilePath: String? = null
    private var toFilePath: String? = null
    private var extensions: List<String> = ArrayList()
    private var mode = Mode.copy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_detail)

        val moveConfig = intent.getParcelableExtra<MoveConfig>("move_config")
        if (moveConfig != null) {
            fromFilePath = moveConfig.fromPath
            toFilePath = moveConfig.toPath
            extensions = moveConfig.extensions
            mode = moveConfig.mode
        }
        updateUI()
    }

    fun onExtensionsEditClicked(v: View) {

        val list = ArrayList<MultiSelectModel>()
        val allExtensions = resources.getStringArray(R.array.extensions)
        for ((index, extension) in allExtensions.withIndex()) {

            list.add(MultiSelectModel(index, extension))
        }

        val listOfSelected = ArrayList<Int>()
        if (extensions.isNotEmpty()) {
            for (multiSelectModel in list) {
                if (extensions.contains(multiSelectModel.name)) {
                    listOfSelected.add(multiSelectModel.id)
                }
            }
        }

        MultiSelectDialog()
                .title("Extensions") //setting title for dialog
                .positiveText(resources.getString(android.R.string.ok))
                .negativeText(resources.getString(android.R.string.cancel))
                .preSelectIDsList(listOfSelected)
                .multiSelectList(list)
                .onSubmit(object : MultiSelectDialog.SubmitCallbackListener {
                    override fun onSelected(selectedIds: ArrayList<Int>, selectedNames: ArrayList<String>, dataString: String) {
                        extensions = selectedNames
                        updateUI()
                    }

                    override fun onCancel() {

                    }
                }).show(supportFragmentManager, "multiSelectDialog")
    }

    fun onToEditClicked(v: View) {
        val properties = DialogProperties()
        properties.selection_type = DialogConfigs.DIR_SELECT
        properties.selection_mode = DialogConfigs.SINGLE_MODE
        if (toFilePath != null) {
            properties.root = File(toFilePath)
        }

        val dialog = FilePickerDialog(this, properties)
        dialog.setTitle("Select to folder")
        dialog.setDialogSelectionListener {
            if (it.size == 1) {
                toFilePath = it[0]
                updateUI()
            }
        }
        dialog.show()
    }

    private fun updateUI() {
        config_detail_value_from.text = fromFilePath ?: ""
        config_detail_value_to.text = toFilePath ?: ""
        config_detail_value_extensions.text = TextUtils.join(", ", extensions)
        when (mode) {
            Mode.copy -> config_detail_value_copy.isChecked = true
            Mode.move -> config_detail_value_move.isChecked = true
        }
    }

    fun onFromEditClicked(v: View) {
        val properties = DialogProperties()
        properties.selection_type = DialogConfigs.DIR_SELECT
        if (fromFilePath != null) {
            properties.root = File(fromFilePath)
        }

        val dialog = FilePickerDialog(this, properties)
        dialog.setTitle("Select from folder")
        dialog.setDialogSelectionListener {
            if (it.size == 1) {
                fromFilePath = it[0]
                updateUI()
            }
        }
        dialog.show()
    }

    fun onSaveClicked(v: View) {
        if (fromFilePath == null) {
            snackbar(config_detail_layout, "From is missing")
            return
        }
        if (toFilePath == null) {
            snackbar(config_detail_layout, "To is missing")
            return
        }
        if (extensions.isEmpty()) {
            snackbar(config_detail_layout, "Extensions are missing")
            return
        }
        var moveConfig = intent.getParcelableExtra<MoveConfig>("move_config")
        if (moveConfig == null) {
            moveConfig = MoveConfig()
        }
        moveConfig.fromPath = fromFilePath
        moveConfig.toPath = toFilePath
        moveConfig.extensions = extensions
        moveConfig.mode = if (config_detail_value_move.isChecked) Mode.move else Mode.copy

        saveMoveConfig(moveConfig)

        finish()
    }
}

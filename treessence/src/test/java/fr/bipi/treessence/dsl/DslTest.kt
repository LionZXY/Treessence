package fr.bipi.treessence.dsl

import android.util.Log
import fr.bipi.treessence.common.filters.NoFilter
import fr.bipi.treessence.console.SystemLogTree
import fr.bipi.treessence.console.ThrowErrorTree
import fr.bipi.treessence.context.GlobalContext.startTimber
import fr.bipi.treessence.context.GlobalContext.stopTimber
import fr.bipi.treessence.file.FileLoggerTree
import fr.bipi.treessence.ui.TextViewTree
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import timber.log.Timber

class DslTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    @Before
    fun setUp() {
        Timber.uprootAll()
    }

    @Test
    fun startTimberDsl() {
        startTimber {
            debugTree().`should be instance of`(Timber.Tree::class.java)

            releaseTree().`should be instance of`(Timber.Tree::class.java)

            tree(
                { s: String, t: Throwable? ->
                },
            ) {
                level = Log.INFO

                filter(NoFilter())

                filter { prio, tag, m, t ->
                    false
                }

                formatter { prio, tag, message ->
                    ""
                }
            }.`should be instance of`(Timber.Tree::class.java)

            logcatTree {
                level = Log.INFO

                filter(NoFilter())

                filter { prio, tag, m, t ->
                    false
                }

                formatter { prio, tag, message ->
                    ""
                }
            }.`should be instance of`(Timber.Tree::class.java)

            fileTree {
                level = Log.INFO
                fileName = "myfile"
                dir = temporaryFolder.newFolder().absolutePath
                sizeLimit = 10
                fileLimit = 1
                appendToFile = true

                filter { prio, tag, m, t ->
                    false
                }

                formatter { prio, tag, message ->
                    ""
                }
            }.`should be instance of`(FileLoggerTree::class.java)

            systemTree {
                level = Log.INFO

                filter(NoFilter())

                filter { prio, tag, m, t ->
                    false
                }

                formatter { prio, tag, message ->
                    ""
                }
            }.`should be instance of`(SystemLogTree::class.java)

            throwErrorTree {
                level = Log.INFO

                filter(NoFilter())

                filter { prio, tag, m, t ->
                    false
                }

                formatter { prio, tag, message ->
                    ""
                }
            }.`should be instance of`(ThrowErrorTree::class.java)

            textViewTree {
                level = Log.INFO
                append = true

                filter(NoFilter())

                filter { prio, tag, m, t ->
                    false
                }

                formatter { prio, tag, message ->
                    ""
                }
            }.`should be instance of`(TextViewTree::class.java)
        }

        Timber.treeCount.`should be equal to`(8)
        stopTimber()
        Timber.treeCount.`should be equal to`(0)
    }

    @Test
    fun debugTree() {
        var tree: Timber.Tree? = null
        startTimber {
            debugTree().also {
                tree = it
            }
        }

        Timber.treeCount.`should be equal to`(1)
        Timber.forest()[0].`should be equal to`(tree)

        stopTimber()

        Timber.treeCount.`should be equal to`(0)
    }

    @Test
    fun releaseTree() {
        var tree: Timber.Tree? = null
        startTimber {
            releaseTree().also {
                tree = it
            }
        }

        Timber.treeCount.`should be equal to`(1)
        Timber.forest()[0].`should be equal to`(tree)

        stopTimber()

        Timber.treeCount.`should be equal to`(0)
    }

    @Test
    fun tree() {
        var tree: Timber.Tree? = null
        val sb = StringBuilder()
        startTimber {
            tree(
                { s: String, t: Throwable? ->
                    sb.append("$s:$t\n")
                },
            ) {
                level = Log.INFO

                filter(NoFilter())

                filter { prio, tag, m, t ->
                    false
                }

                formatter { prio, tag, message ->
                    "$prio:$tag:$message"
                }
            }.also {
                tree = it
            }
        }

        Timber.treeCount.`should be equal to`(1)
        Timber.forest()[0].`should be equal to`(tree)

        Timber.tag("tag").i("message")

        sb.toString().`should be equal to`("4:tag:message:null\n")

        stopTimber()

        Timber.treeCount.`should be equal to`(0)
    }
}

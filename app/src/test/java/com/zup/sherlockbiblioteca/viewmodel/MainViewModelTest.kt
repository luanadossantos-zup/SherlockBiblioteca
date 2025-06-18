import android.app.Application
import android.content.res.AssetManager
import com.zup.sherlockbiblioteca.model.Book
import com.google.gson.Gson
import com.zup.sherlockbiblioteca.viewmodel.MainViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application
    private lateinit var assetManager: AssetManager

    private val fakeBooks = listOf(
        Book("A Study in Scarlet", 1887, "Detective novel", "Romance"),
        Book("The Blue Carbuncle", 1892, "Short story", "Conto"),
        Book("The Sign of Four", 1890, "Detective novel", "Conto"),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)
        assetManager = mockk(relaxed = true)

        every { application.assets } returns assetManager
        every { assetManager.open(any()) } returns
                fakeBooks.toJsonInputStream()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carrega livros do json ao iniciar`() = runTest {
        val viewModel = MainViewModel(application)
        advanceUntilIdle() // Aguarda todas as corrotinas
        assertEquals(3, viewModel.visibleBooks.value.size)
    }

    @Test
    fun `filtra livros por texto`() = runTest {
        val viewModel = MainViewModel(application)
        advanceUntilIdle()
        viewModel.updateQuery("A Study in Scarlet")
        advanceTimeBy(400) // debounce
        assertEquals(1, viewModel.visibleBooks.value.size)
        assertEquals("A Study in Scarlet", viewModel.visibleBooks.value.first().title)
    }

    @Test
    fun `filtra livros por formato Conto`() = runTest {
        val viewModel = MainViewModel(application)
        advanceUntilIdle()
        viewModel.filterShortStories()
        assertEquals(2, viewModel.visibleBooks.value.size)
    }

    @Test
    fun `limpa filtro de formato`() = runTest {
        val viewModel = MainViewModel(application)
        advanceUntilIdle()
        viewModel.filterShortStories()
        viewModel.cleanFilterFormat()
        assertEquals(3, viewModel.visibleBooks.value.size)
    }
}

// Extensão para simular InputStream de JSON
fun List<Book>.toJsonInputStream(): java.io.InputStream {
    val json = Gson().toJson(this)
    return json.byteInputStream()
}
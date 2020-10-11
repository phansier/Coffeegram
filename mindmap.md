# Dark sides of Jetpack Compose

## How to start. Common features and tools.

### Themes and styling

- Material Theming

	- @Composable MaterialTheme

		- colors =

			- Colors class
			- lightColors()
			- darkColors()

		- typography =

			- Typography class

		- shapes =

	- @Composable fun CustomTheme (content: @Composable () -> Unit) { MaterialTheme(...)}

		- Whole app or some subtree can be wrapped with CustomTheme

			- nested themes

		- darkTheme: Boolean = isSystemInDarkTheme()

			- if (darkTheme) DarkColors else LightColors

	- Dynamic Theming

- MDC Compose Theme Adapter library

	- https://goo.gle/mdc-compose-theme-adapter

- Set Statusbar color from Compose

	- https://goo.gle/compose-sysui-controller

- WindowInsets

	- Not currently in Compose
	- Workaround

		- https://goo.gle/compose-insets

- Downloadable fonts are not supported
- Compose extension for CoilImage loading

### Async operations

- onCommit(params of composable) - when Composable is recomposed (params were changed)
- onDispose - if it recomposed again
- may be useful for ports of image loading libraries
- Back button

	- Owl sample app

- Runtime permissions

	- Jetsurvey sample app

### Features

- Modifiers

	- Применяются в порядке от последнего (нижнего)
	- Good convention

		- modifier: Modifier = Modifier - to add default modifier for custom view
		- modifier should be first param to able be passed without name

- remember function

	- saves the state between recompositions

		- val ringColor = remember {randomColor()}

	- inputs parameter for cache invalidation

		- val annotated  = remember(text) {parseAnnotated(text)}

- AnnotatedString

	- HTML
	- Markdown?

- viewModel<ChatViewModel>()

	- some kind of DI function, providing needed viewModel - ChatViewModel instance
	- to avoid passing it into parameters of composable function

- Layouts

	- Column
	- Row
	- Stack
	- Constraint Layout

- @Composable fun Layout()

	- custom layouts
	- children: @Composable () -> Unit
	- modifier: Modifier = Modifier
	- measureBlock: MeasureBlock

		- measurables: List<Measurable>, constraints:Constraints ->

			- Constraints

				- min/max width/height

		- Measure each item

			- val placeable = measurables.map {it.measure(constraints)}
			- Compose disallow to measure items twice

				- Performance guarantees

		- Call layout()

			- layout(width =, height = )

		- Place each item

			- placeables.forEach {it,place(x=, y=)}

	- Пример ViewPager: https://jorgecastillo.dev/compose-viewpager

- Animations

	- animate

		- val radius = animate(if (selected) 28.dp else 0.dp)

	- Transitions

		- val trdef = transitionDefinition{ state (enum1){} state(enum2) {} }
		- val state =transition( definition = trdef, toState = selection)
		- val prop = state[Prop]

	- Animation Inspector - WIP

## Differences between the internal structure and the existing View hierarchy

### No default widget style

### No concept of default styles declared in theme

### Declarative

@Composable fun StatelessTextField() {
  TextField(
    value = "Hello", //will not react on input symbols
    onValueChange = {}
  )
}
//
@Composable fun StatefullTextField() {
  var text by remember {mutableStateOf(“”)}
  TextField(
    value = text,
    onValueChange = { text = it}
  )
}

- Views do not keep any state by themselves
- <- here is a code
- State

	- Composable function get subscribed to State instance any time the value property is read during its execution
	- by keyword

		- property delegate
		-  var text by remember {mutableStateOf(“”)}
  TextField(
    value = text,
    onValueChange = { text = it}
  )
		-  var text = remember {mutableStateOf(“”)}
  TextField(
    value = text.getValue(),
    onValueChange = { text.setValue(it)}
  )
		- inline operator fun State.getValue()
		- inline operator fun MutableState.setValue()

	- Rule of thumb

		- Put state in lowest common ancestor of its consumers
		- Single source of State's Truth

- Simple theming

	- easy to setup themes
	- easy to find out how components use theme attributes

- Modularity

	- Compose

		- Material

			- Can be replaced by custom design system component

		- Foundation
		- UI
		- Runtime

### Built on Kotlin

- Kotlin Compiler Plugin
- Scopes to provide type safety

	- # Row -> RowScope -> gravity(CenterVertically)
	- More explicit things than non-obvious

### Unbundled from OS

- Feature improvements & bug fixes

### Single Responsibility

- content: @Composable ()->Unit

### Same language allows to decrease coupling between UI-code (XML) and other code (like activity - findViewById)

### Rethinking the Legacy

### Simple recyclerView

## How to cook it. Examples of compatible architectures.

### weekly newsletter

- https://jetc.dev/

## Multiplatform opportunities. Launch in Desktop.

### Launch in Desktop

- https://arnestockmans.be/blog/build-run-jetpack-compose-for-desktop
- https://github.com/androidx/androidx/tree/androidx-master-dev/compose/desktop/desktop
- gradle.properties

	- https://github.com/JetBrains/skiko
	- skiko => JDK 11+

- ./gradlew :compose:desktop:desktop:samples:run3

### Other signs

- Jetcaster

	- Dispatchers are injecting through DI

		- Dispatchers.Main
		- Dispatchers.IO
		- It makes sense for launching Coroutines in iOS

## Comparison with other declarative UI-frameworks

### Flutter

- Modifiers - significantly reduce code
- State management is more sophisticated

	- No division on stateful and stateless widgets

- Kotlin)

## Current state. Is ready for production?

### Migration strategies

- New compose-only screen
- Compose and Views mixed

	- MapView is not available

- Migrate whole screen

### How start using Compose

- Activity

	- setContent()

- Fragments

	- onCreateView() = ComposeView.apply{setContent()}

- Layouts

	- ComposeView

		- findViewById().setContent()

	- Use unique ids for saveinstancestate work

### How use Composable inside View

- AndroidView
- AndroidViewBinding

	- for xml

### Access resources from Composable

- stringResource()
- dimensionResource()
- vectorResource()
- colorResource()
- Ambient

	- ContextAmbient
	- ViewAmbient
	- LifecycleOwnerAmbient
	- ConfigurationAmbient
	- Similar to Service Locator for common things passed through the tree

		- val JC = ambientOf<JCP>{}

			- staticAmbientOf

		- Providers(JC provides colors){}
		- val colors: JCP get() = JC.current

### @Composable fun viewmodel()

### Asynchronous frameworks

- LiveData.observeAsState()
- Flow.collectAsState()
- Observable.subscribeAsState()

### var insState by savedInstanceState<Int> {0}

- @Parcelize data class (): Parcelable
- listSaver

### Performance

- @Immutable

	- Используется для обознаения data class с только val полями
	- Allows to not recalculate diff to Compose?

## UI testing

### Testing Compose only code

- @get:Rule val rule =  createComposeRule()
- @Test {
rule.setContent()
}
- onNodeWithText("").assertIsDisplayed()

### Testing mixed code

- ActivityScenario Rule -> createAndroidComposeRule()

	- Espresso
	- Espresso will wait for compose become Idle

### Screenshot testing?

- val screenshot = onRoot().captureToBitmap()
- assertScreenshotMatches(screenshot, "cicle_100")
- https://blog.karumi.com/jetpack-compose-screenshot-testing-with-shot/

### Animations testing

- composeTestRule.clockTestRule

## Additional questions

### Navigation

- Jetpack Navigation - in progress
- Custom routers (example in JetNews)

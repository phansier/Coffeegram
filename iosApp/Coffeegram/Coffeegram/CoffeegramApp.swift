import SwiftUI
import SwiftData

@main
struct CoffeegramApp: App {
    var sharedModelContainer: ModelContainer = {
        let schema = Schema([
            CoffeeDrink.self,
        ])
        let modelConfiguration = ModelConfiguration(schema: schema, isStoredInMemoryOnly: false)

        do {
            return try ModelContainer(for: schema, configurations: [modelConfiguration])
        } catch {
            fatalError("Could not create ModelContainer: \(error)")
        }
    }()

    var body: some Scene {
        WindowGroup {
            CoffeeTrackerView()
        }
        .modelContainer(sharedModelContainer)
    }
}

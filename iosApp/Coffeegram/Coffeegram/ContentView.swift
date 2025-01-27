import SwiftUI
import SwiftData

class CoffeeTrackerViewModel: ObservableObject {
    @Published var drinks: [CoffeeDrink] = [
        CoffeeDrink(name: "Espresso", icon: "cup.and.saucer.fill", count: 0),
        CoffeeDrink(name: "Cappuccino", icon: "cup.and.saucer", count: 0),
        CoffeeDrink(name: "Latte", icon: "mug.fill", count: 0),
        CoffeeDrink(name: "Americano", icon: "mug", count: 0)
    ]
    
    func increment(drinkId: UUID) {
        if let index = drinks.firstIndex(where: { $0.id == drinkId }) {
            drinks[index].count += 1
        }
    }
    
    func decrement(drinkId: UUID) {
        if let index = drinks.firstIndex(where: { $0.id == drinkId }),
           drinks[index].count > 0 {
            drinks[index].count -= 1
        }
    }
}


struct CoffeeTrackerView: View {
    @StateObject private var viewModel = CoffeeTrackerViewModel()
    
    var body: some View {
        
            List {
                ForEach(viewModel.drinks) { drink in
                    HStack {
                        
                        // Drink info
                        HStack {
                            Image(systemName: drink.icon)
                                .foregroundColor(.brown)
                                .imageScale(.large)
                            Text(drink.name)
                                .font(.body)
                            Spacer()
                            
                        }
                        
                        // Decrement button
                        Button(action: {
                            viewModel.decrement(drinkId: drink.id)
                        }) {
                            Image(systemName: "minus.circle.fill")
                                .foregroundColor(.red)
                                .imageScale(.large)
                        }
                        Text("\(drink.count)")
                            .font(.headline)
                            .monospacedDigit()
                        // Increment button
                        Button(action: {
                            viewModel.increment(drinkId: drink.id)
                        }) {
                            Image(systemName: "plus.circle.fill")
                                .foregroundColor(.green)
                                .imageScale(.large)
                        }
                    }
                    .padding(.vertical, 4)
                
            }
            
        }
    }
}

#Preview {
    CoffeeTrackerView()
}

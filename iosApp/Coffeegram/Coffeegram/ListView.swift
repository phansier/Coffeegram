import SwiftUI
import SwiftData

struct ListView: View {
    @StateObject private var viewModel = ListViewModel()

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
    ListView()
}

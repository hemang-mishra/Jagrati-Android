//
//  ContentView.swift
//  iosApp
//
//  Created by Hemang Mishra on 21/12/25.
//

import SwiftUI
import multiplatform_kit

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            let str = Platform_iosKt.platform()
            Text("Hello, \(str)")
        }
        .padding()
    }
}

#Preview {
    ContentView()
}

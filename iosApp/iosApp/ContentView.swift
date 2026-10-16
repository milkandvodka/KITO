import UIKit
import SwiftUI
import ComposeApp

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

final class TabBarOverlayWindow: UIWindow {
    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        guard let tabVC = rootViewController as? UITabBarController,
              tabVC.tabBar.alpha > 0.01,
              !tabVC.tabBar.isHidden else { return nil }

        let tabBarFrameInWindow = tabVC.tabBar.convert(tabVC.tabBar.bounds, to: nil)
        guard tabBarFrameInWindow.contains(point) else { return nil }

        return super.hitTest(point, with: event)
    }
}

final class KitoTabBarController: UITabBarController, UITabBarControllerDelegate {

    private let kAccent = UIColor(red: 1.0, green: 165.0 / 255.0, blue: 116.0 / 255.0, alpha: 1.0)

    override func viewDidLoad() {
        super.viewDidLoad()
        delegate = self
        view.backgroundColor = .clear

        let items: [(title: String, icon: String)] = [
            ("Home",       "house.fill"),
            ("Attendance", "checkmark.circle.fill"),
            ("Calendar",   "calendar"),
            ("Settings",   "gearshape.fill"),
        ]
        setViewControllers(items.enumerated().map { index, item in
            let vc = UIViewController()
            vc.view.backgroundColor = .clear
            vc.view.isUserInteractionEnabled = false
            vc.tabBarItem = UITabBarItem(title: item.title,
                                         image: UIImage(systemName: item.icon),
                                         tag: index)
            return vc
        }, animated: false)

        tabBar.tintColor = kAccent
        tabBar.unselectedItemTintColor = UIColor.white.withAlphaComponent(0.55)

        if #unavailable(iOS 26) {
            let appearance = UITabBarAppearance()
            appearance.configureWithDefaultBackground()
            tabBar.standardAppearance = appearance
            tabBar.scrollEdgeAppearance = appearance
        }

        if #available(iOS 26, *) {
            tabBarMinimizeBehavior = .onScrollDown
        }

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleNavBarState(_:)),
            name: .navBarStateChanged,
            object: nil
        )
    }

    @objc private func handleNavBarState(_ note: Notification) {
        guard let state = note.object as? NavBarState else { return }
        if state.selectedIndex != selectedIndex { selectedIndex = state.selectedIndex }
        UIView.animate(withDuration: 0.35, delay: 0,
                       usingSpringWithDamping: 0.85, initialSpringVelocity: 0) {
            self.view.window?.alpha = state.isVisible ? 1 : 0
            self.tabBar.transform = state.isVisible
                ? .identity
                : CGAffineTransform(translationX: 0, y: self.tabBar.bounds.height + 48)
        }
    }

    func tabBarController(_ tabBarController: UITabBarController,
                          didSelect viewController: UIViewController) {
        IosNavBridge.shared.selectTab(index: Int32(tabBarController.selectedIndex))
    }
}

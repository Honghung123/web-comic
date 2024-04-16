import { Fragment } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import DefaultLayout from "./layouts/DefaultLayout";
import { publicRoutes as routes } from "./routes";

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                {routes.map((route, index) => {
                    const Component = route.component;
                    let Layout = DefaultLayout;
                    if (route?.layout) {
                        Layout = route.layout;
                    } else if (route.layout === null) {
                        Layout = Fragment;
                    }
                    const Page = (
                        <Layout>
                            <Component />
                        </Layout>
                    );
                    return <Route key={index} path={route.path} element={Page} />;
                })}
            </Routes>
        </BrowserRouter>
    );
}

import Header from "../components/Header";
import Content from "../components/Content";
import Footer from "../components/Footer";
import Navigation from "../components/Navigation";

export default function DefaultLayout({ children }) {
    return (
        <>
            <Header />
            <Navigation />
            <Content>{children}</Content>
            <Footer />
        </>
    );
}

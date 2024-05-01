import Header from '../components/Header';
import Content from '../components/Content';
import Footer from '../components/Footer';

export default function DefaultLayout({ children }) {
    return (
        <>
            <Header />
            <Content>{children}</Content>
            <Footer />
        </>
    );
}

import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import Header from '../components/Header';
import Content from '../components/Content';
import Footer from '../components/Footer';

function DefaultLayout({ children }) {
    return (
        <>
            <ToastContainer />
            <Header />
            <Content>{children}</Content>
            <Footer />
        </>
    );
}

export default DefaultLayout;

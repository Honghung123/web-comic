// Layouts
// import { HeaderOnlyLayout } from "./components/Layout";

// Import component page
import Home from "./../pages/Home";
import ComicInfo from "./../pages/ComicInfo";
import Reading from "./../pages/Reading";

// Public routes
const publicRoutes = [
    { path: "/", component: Home },
    { path: "/info", component: ComicInfo },
    { path: "/reading", component: Reading },
];

// Private routes
const privateRoutes = [];

export { publicRoutes, privateRoutes };

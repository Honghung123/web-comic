// Layouts
// import { HeaderOnlyLayout } from "./components/Layout";

// Import component page
import Home from "./../pages/Home";
import ComicInfo from "./../pages/ComicInfo";
import Reading from "./../pages/Reading";
import AuthorGenreListComics from '../pages/AuthorGenreListComics';

// Public routes
const publicRoutes = [
    { path: "/", component: Home },
    { path: "/info/:tagId", component: ComicInfo },
    { path: "/reading/:tagId/:chapter", component: Reading },
    { path: "/author/:authorId", component: AuthorGenreListComics },
    { path: "/genre/:genreId", component: AuthorGenreListComics },
];

// Private routes
const privateRoutes = [];

export { publicRoutes, privateRoutes };

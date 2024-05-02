import SettingServer from '../../components/SettingServer';
import ListComics from '../../components/ListComics';

export default function Home() {
    return (
        <div className="px-4 py-8">
            <SettingServer />
            <ListComics />
        </div>
    );
}

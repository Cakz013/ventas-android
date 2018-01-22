package tpoffline;

import tpoffline.widget.SearchTransformer;

/**
 * Created by Cesar on 6/30/2017.
 */

public class SearchTransformUtil {

    public static final SearchTransformer TRANSFORM_TO_STRING = new SearchTransformer() {

        @Override
        public String transformForSearch(Object value) {

            return value.toString();
        }
    };

}

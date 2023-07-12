package cn.lioyan.test.autoconfigure.config.scope;
import cn.lioyan.autoconfigure.config.scope.NameReference;
/**
 * {@link NameReferenceTest}
 *
 * @author com.lioyan
 * @date 2023/7/12  9:22
 */
public class NameReferenceTest
{

    public static void main(String[] args)
    {

        NameReference nameReference = new NameReference();
        nameReference.addAlias("a","b");
        nameReference.addAlias("b","c");
        nameReference.addAlias("c","d");

        String[] aliasGroundName = nameReference.getAliasGroundName();
        for (String s : aliasGroundName)
        {
            System.out.println(s);
        }
        System.out.println(nameReference.getAlias("a"));

    }
}

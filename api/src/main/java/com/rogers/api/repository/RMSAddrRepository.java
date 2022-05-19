package com.rogers.api.repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RMSAddrRepository {

    @Autowired
    private EntityManager entityManager;

    public Map<String, String> fetchRMSAddrByModuleAndAddrType() {
        Map<String, String> map = new HashMap<>();
        Query query = entityManager.createNativeQuery("SELECT key_value_1, state FROM rmsdas.das_wv_addr WHERE module = 'ST' AND addr_type = '01'");
        List<Object[]> list = query.getResultList();

        if (!list.isEmpty()) {
            for (Object[] result : list) {
                map.put(result[0].toString(), result[1].toString());
            }
        }
        return map;
    }

    public List<String> fetchRMSItemLocData(){
        Query query = entityManager.createNativeQuery("select * \n" +
                "  from (select distinct 'UPDATE|ITEM_TAX_GROUP|'||item||'|*|*|'||nvl(decode(taxable_ind, 'Y',1, 0),0) \n" +
                "          from (select item, max(taxable_ind) taxable_ind\n" +
                "                  from rmsdas.das_wv_item_loc\n" +
                "                 where loc_type = 'S'\n" +
                "                   and nvl(last_update_datetime,create_datetime) > (select last_run_datetime \n" +
                "                                                                      from RCI_TAX_FILE_CONFIG \n" +
                "                                                                     where file_name = 'rogers_tax_group.mnt')\n" +
                "                group by item\n" +
                "                )\n" +
                "        union all\n" +
                "        select 'UPDATE|ITEM_TAX_GROUP|'||item||'|STORE|'||loc||'|'||nvl(decode(taxable_ind, 'Y',1, 0),0)  \n" +
                "          from rmsdas.das_wv_item_loc\n" +
                "         where loc_type = 'S'\n" +
                "           and item in (select item\n" +
                "                          from rmsdas.das_wv_item_loc\n" +
                "                         where loc_type = 'S'\n" +
                "                           and nvl(last_update_datetime,create_datetime) > (select last_run_datetime \n" +
                "                                                                              from RCI_TAX_FILE_CONFIG \n" +
                "                                                                             where file_name = 'rogers_tax_group.mnt')\n" +
                "                        )\n" +
                "       )\n" +
                "order by 1");
        List<String> itemLocList = query.getResultList();

        return itemLocList;
    }
}
